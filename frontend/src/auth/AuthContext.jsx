import { useState } from "react";

import AuthContext from "./AuthContext";
import { loginUser, logoutUser } from "../api/authApi";

const VALID_ROLES = [
  "FIELD_OFFICER",
  "DISTRICT_OFFICER",
  "FINANCE_APPROVER",
  "ADMIN",
];

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(
    () => sessionStorage.getItem("token")
  );

  const [role, setRole] = useState(
    () => sessionStorage.getItem("role")
  );

  const [username, setUsername] = useState(
    () => sessionStorage.getItem("username")
  );

  const login = async (usernameValue, password) => {
    const data = await loginUser(
      usernameValue,
      password
    );

    console.log("Login response:", data);

    const receivedToken = data?.token;

    const receivedRole = String(
      data?.role || ""
    )
      .replace("ROLE_", "")
      .toUpperCase();

    const receivedUsername =
      data?.username || usernameValue;

    if (!receivedToken) {
      throw new Error(
        "Authentication token was not received from the server."
      );
    }

    if (!receivedRole) {
      throw new Error(
        "User role was not received from the server."
      );
    }

    if (!VALID_ROLES.includes(receivedRole)) {
      throw new Error(
        `Invalid user role received: ${receivedRole}`
      );
    }

    sessionStorage.setItem(
      "token",
      receivedToken
    );

    sessionStorage.setItem(
      "role",
      receivedRole
    );

    sessionStorage.setItem(
      "username",
      receivedUsername
    );

    setToken(receivedToken);
    setRole(receivedRole);
    setUsername(receivedUsername);

    return {
      token: receivedToken,
      role: receivedRole,
      username: receivedUsername,
    };
  };

  const logout = () => {
    logoutUser();

    setToken(null);
    setRole(null);
    setUsername(null);
  };

  const hasRole = (allowedRoles) => {
    if (!role) {
      return false;
    }

    const roles = Array.isArray(allowedRoles)
      ? allowedRoles
      : [allowedRoles];

    return roles.includes(role);
  };

  const authValue = {
    token,
    role,
    username,
    loading: false,
    isAuthenticated: Boolean(token),
    login,
    logout,
    hasRole,
  };

  return (
    <AuthContext.Provider value={authValue}>
      {children}
    </AuthContext.Provider>
  );
};