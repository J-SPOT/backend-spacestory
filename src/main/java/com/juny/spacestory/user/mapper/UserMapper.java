package com.juny.spacestory.user.mapper;

import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.dto.ResLookUpUser;
import com.juny.spacestory.user.dto.ResLookUpUsers;
import com.juny.spacestory.user.dto.ResModifyUser;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface UserMapper {

  ResLookUpUser toResLookUpUser(User user);

  ResModifyUser toResModifyUser(User user);

  ResLookUpUsers toResLookUpUsers(User user);

  default Page<ResLookUpUsers> toResLookUpUsers(Page<User> users) {
    return users.map(this::toResLookUpUsers);
  }
}
