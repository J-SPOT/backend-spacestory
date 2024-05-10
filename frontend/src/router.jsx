import { createBrowserRouter } from "react-router-dom";
import Login from "./page/Login";

const router = createBrowserRouter([
  {
    path: "/",
    element: <div>Hello world!</div>,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/register",
    element: <div>register</div>,
  },
]);

export default router;
