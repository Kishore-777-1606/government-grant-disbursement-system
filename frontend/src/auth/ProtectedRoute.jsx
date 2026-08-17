import {
  Navigate,
  Outlet,
  useLocation,
} from "react-router-dom";

import { useAuth } from "./useAuth";

const ProtectedRoute = ({ allowedRoles = [] }) => {
  const {
    isAuthenticated,
    role,
  } = useAuth();

  const location = useLocation();

  // User is not logged in
  if (!isAuthenticated) {
    return (
      <Navigate
        to="/"
        replace
        state={{ from: location }}
      />
    );
  }

  // User is logged in but does not have permission
  if (
    allowedRoles.length > 0 &&
    !allowedRoles.includes(role)
  ) {
    return (
      <Navigate
        to="/unauthorized"
        replace
      />
    );
  }

  // User is authenticated and authorized
  return <Outlet />;
};

export default ProtectedRoute;