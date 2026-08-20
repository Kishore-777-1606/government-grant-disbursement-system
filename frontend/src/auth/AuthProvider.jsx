import {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from "react";

import AuthContext from "./AuthContext.js";
import { loginUser } from "../api/authApi";

const TOKEN_KEY = "authToken";
const ROLE_KEY = "authRole";
const USERNAME_KEY = "authUsername";

// Legacy shared key — every page built before this auth system was merged
// (Verification, Beneficiaries, Applications, Disbursement, AuditLog, and
// the axios interceptor in services/api.js) reads the logged-in user from
// this single localStorage key as one JSON object. Writing it here keeps
// all of that working without having to rewrite every page to use useAuth().
const LEGACY_USER_KEY = "user";

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
    localStorage.removeItem(LEGACY_USER_KEY);
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

  // Keeps the legacy shared localStorage("user") key in sync whenever
  // auth state changes — not just at login — so a page refresh mid-session
  // doesn't leave it stale for the pages that still read from it directly.
  useEffect(() => {
    if (token && role) {
      try {
        localStorage.setItem(
          LEGACY_USER_KEY,
          JSON.stringify({
            token,
            username,
            role,
          })
        );
      } catch {
        // Ignore storage errors
      }
    }
  }, [token, role, username]);

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

      const receivedFullName =
        response?.fullName ||
        response?.user?.fullName ||
        receivedUsername;

      const receivedUserId =
        response?.userId ||
        response?.user?.userId ||
        null;

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

        // Legacy shared shape — kept in sync so every existing page's
        // localStorage.getItem("user") role-check keeps working.
        localStorage.setItem(
          LEGACY_USER_KEY,
          JSON.stringify({
            token: receivedToken,
            userId: receivedUserId,
            username: receivedUsername,
            fullName: receivedFullName,
            role: receivedRole,
          })
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