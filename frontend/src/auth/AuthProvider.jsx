import {
  useCallback,
  useMemo,
  useState,
} from "react";

import AuthContext from "./AuthContext.js";
import { loginUser } from "../api/authApi";

const TOKEN_KEY = "authToken";
const ROLE_KEY = "authRole";
const USERNAME_KEY = "authUsername";

const getStoredValue = (key) => {
  try {
    return sessionStorage.getItem(key);
  } catch {
    return null;
  }
};

const clearStoredAuth = () => {
  try {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    sessionStorage.removeItem(USERNAME_KEY);
  } catch {
    // Ignore storage errors
  }
};

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() =>
    getStoredValue(TOKEN_KEY)
  );

  const [role, setRole] = useState(() =>
    getStoredValue(ROLE_KEY)
  );

  const [username, setUsername] = useState(() =>
    getStoredValue(USERNAME_KEY)
  );

  const login = useCallback(
    async (usernameValue, password) => {
      const response = await loginUser(
        usernameValue,
        password
      );

      const receivedToken =
        response?.token ||
        response?.accessToken ||
        response?.jwt;

      const receivedRole =
        response?.role ||
        response?.user?.role;

      const receivedUsername =
        response?.username ||
        response?.user?.username ||
        usernameValue;

      if (!receivedToken) {
        throw new Error(
          "Login response does not contain a token."
        );
      }

      if (!receivedRole) {
        throw new Error(
          "Login response does not contain a user role."
        );
      }

      try {
        sessionStorage.setItem(
          TOKEN_KEY,
          receivedToken
        );

        sessionStorage.setItem(
          ROLE_KEY,
          receivedRole
        );

        sessionStorage.setItem(
          USERNAME_KEY,
          receivedUsername
        );
      } catch (error) {
        throw new Error(
          "Unable to save login session.",
          {
            cause: error,
          }
        );
      }

      setToken(receivedToken);
      setRole(receivedRole);
      setUsername(receivedUsername);

      return response;
    },
    []
  );

  const logout = useCallback(() => {
    clearStoredAuth();

    setToken(null);
    setRole(null);
    setUsername(null);
  }, []);

  const isAuthenticated = Boolean(
    token && role
  );

  const value = useMemo(
    () => ({
      token,
      role,
      username,
      isAuthenticated,
      login,
      logout,
    }),
    [
      token,
      role,
      username,
      isAuthenticated,
      login,
      logout,
    ]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}