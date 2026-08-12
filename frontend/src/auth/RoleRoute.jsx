import {
  Navigate,
  Outlet,
  useLocation,
} from "react-router-dom";

import { useAuth } from "./useAuth";

function RoleRoute({ allowedRoles = [] }) {
  const {
    role,
    isAuthenticated,
  } = useAuth();

  const location = useLocation();

  // User is not logged in
  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location }}
      />
    );
  }

  // Normalize role received from backend
  const currentRole = role?.toUpperCase();

  // Check whether user's role is allowed
  const hasPermission =
    allowedRoles
      .map((allowedRole) =>
        allowedRole.toUpperCase()
      )
      .includes(currentRole);

  // Logged in but not authorized
  if (!hasPermission) {
    return (
      <Navigate
        to="/dashboard"
        replace
      />
    );
  }

  // Authorized
  return <Outlet />;
}

export default RoleRoute;