package com.juny.spacestory.user.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.global.exception.hierarchy.user.TotpCodeInvalidException;
import com.juny.spacestory.global.exception.hierarchy.user.TotpNotActivatedException;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import com.juny.spacestory.user.domain.TotpVerification;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.TotpVerificationCodeRepository;
import com.juny.spacestory.user.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TotpVerificationService {

  private final GoogleAuthenticator googleAuth = new GoogleAuthenticator();
  private final TotpVerificationCodeRepository codeRepository;
  private final UserRepository userRepository;

  private final String ISSUER = "SpaceStory";
  private final String EMAIL_IS_NULL_OR_EMPTY = "Email is null or empty";
  private final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  @Transactional
  public String createTotpQrCode(String email) throws WriterException, IOException {

    validateEmail(email);

    userRepository.findByEmail(email).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    GoogleAuthenticatorKey authenticationKey = googleAuth.createCredentials();
    String secret = authenticationKey.getKey();
    codeRepository.deleteByEmail(email);
    codeRepository.save(new TotpVerification(email, secret));
    String otpAuthTotpURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUER, email,
      authenticationKey);

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthTotpURL, BarcodeFormat.QR_CODE, 200, 200);
    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(qrImage, "PNG", baos);
    byte[] imageBytes = baos.toByteArray();
    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

    return "data:image/png;base64," + base64Image;
  }

  private void validateEmail(String email) {

    if (email == null || email.trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
        ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, EMAIL_IS_NULL_OR_EMPTY);
    }

    if (!pattern.matcher(email).matches()) {

      log.error("userInvalidEmail: {}", email);
      throw new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL);
    }
  }

  public void verifyTotpCode(String email, int code) {
    User user = userRepository.findByEmail(email).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    TotpVerification totp = codeRepository.findByEmail(email).orElseThrow(
      () -> new TotpNotActivatedException(ErrorCode.TOTP_NOT_ACTIVATED));

    boolean authorize = googleAuth.authorize(totp.getSecret(), code);
    if (!authorize) {
      throw new TotpCodeInvalidException(ErrorCode.TOTP_CODE_INVALID);
    }
    if (!user.getIsTotpEnabled()) {
      user.setTotpEnable();
    }
  }
}
