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
  /*
   * Restore authentication directly during
   * initial state creation.
   *
   * This avoids setState() inside useEffect.
   */
  const [token, setToken] = useState(() =>
    getStoredValue(TOKEN_KEY)
  );

  const [role, setRole] = useState(() =>
    getStoredValue(ROLE_KEY)
  );

  const [username, setUsername] = useState(() =>
    getStoredValue(USERNAME_KEY)
  );

  /*
   * Login
   */
  const login = useCallback(
    async (usernameValue, password) => {
      const response = await loginUser(
        usernameValue,
        password
      );

      /*
       * Support different possible backend
       * token property names.
       */
      const receivedToken =
        response?.token ||
        response?.accessToken ||
        response?.jwt;

      /*
       * Support role directly or inside user.
       */
      const receivedRole =
        response?.role ||
        response?.user?.role;

      /*
       * Support username directly or inside user.
       */
      const receivedUsername =
        response?.username ||
        response?.user?.username ||
        usernameValue;

      /*
       * Validate token.
       */
      if (!receivedToken) {
        throw new Error(
          "Login response does not contain a token."
        );
      }

      /*
       * Validate role.
       */
      if (!receivedRole) {
        throw new Error(
          "Login response does not contain a user role."
        );
      }

      /*
       * Store authentication information.
       */
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

      /*
       * Update React state.
       */
      setToken(receivedToken);
      setRole(receivedRole);
      setUsername(receivedUsername);

      return response;
    },
    []
  );

  /*
   * Logout
   */
  const logout = useCallback(() => {
    clearStoredAuth();

    setToken(null);
    setRole(null);
    setUsername(null);
  }, []);

  /*
   * Authentication status.
   */
  const isAuthenticated = Boolean(
    token && role
  );

  /*
   * Context value.
   */
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