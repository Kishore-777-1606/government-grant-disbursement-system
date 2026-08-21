import { useState } from "react";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Container,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import {
  Visibility,
  VisibilityOff,
  LockOutlined,
} from "@mui/icons-material";

import { useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "../../auth/useAuth";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();

  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    const trimmedUsername = username.trim();

    if (!trimmedUsername) {
      setError("Please enter your username.");
      return;
    }

    if (!password) {
      setError("Please enter your password.");
      return;
    }

    try {
      setLoading(true);

      const response = await login(trimmedUsername, password);

      // If the user was redirected here from a specific page (e.g. their
      // session expired mid-task), send them back there. Otherwise, land
      // them on a page their role can actually see — Dashboard is
      // District Officer/Finance Approver/Admin only, so a Field Officer
      // landing there would immediately bounce to /unauthorized.
      const roleDefaultLandingPage =
        response?.role === "FIELD_OFFICER"
          ? "/applications"
          : "/dashboard";

      const from =
        location.state?.from?.pathname ||
        roleDefaultLandingPage;

      navigate(from, {
        replace: true,
      });
    } catch (err) {
      console.error("Login failed:", err);

      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Login failed. Please check your credentials.";

      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background:
          "linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%)",
        px: 2,
      }}
    >
      <Container maxWidth="sm">
        <Paper
          elevation={8}
          sx={{
            p: {
              xs: 3,
              sm: 5,
            },
            borderRadius: 4,
          }}
        >
          {/* Login Icon */}
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              mb: 2,
            }}
          >
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: "50%",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "primary.main",
                color: "white",
              }}
            >
              <LockOutlined fontSize="large" />
            </Box>
          </Box>

          {/* Heading */}
          <Typography
            variant="h4"
            fontWeight="bold"
            gutterBottom
            sx={{ textAlign: "center" }}
          >
            Welcome Back
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              mb: 3,
              textAlign: "center",
            }}
          >
            Sign in to the Government Grant
            Disbursement System
          </Typography>

          {/* Error */}
          {error && (
            <Alert
              severity="error"
              sx={{ mb: 2 }}
              onClose={() => setError("")}
            >
              {error}
            </Alert>
          )}

          {/* Login Form */}
          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
          >
            <TextField
              fullWidth
              label="Username"
              value={username}
              onChange={(event) =>
                setUsername(event.target.value)
              }
              margin="normal"
              autoComplete="username"
              disabled={loading}
              autoFocus
            />

            <TextField
              fullWidth
              label="Password"
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              margin="normal"
              autoComplete="current-password"
              disabled={loading}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      type="button"
                      onClick={() =>
                        setShowPassword(
                          (previous) => !previous
                        )
                      }
                      edge="end"
                      disabled={loading}
                      aria-label={
                        showPassword
                          ? "Hide password"
                          : "Show password"
                      }
                    >
                      {showPassword ? (
                        <VisibilityOff />
                      ) : (
                        <Visibility />
                      )}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                mt: 3,
                py: 1.4,
                borderRadius: 2,
                fontWeight: "bold",
              }}
            >
              {loading ? (
                <>
                  <CircularProgress
                    size={22}
                    color="inherit"
                    sx={{ mr: 1 }}
                  />
                  Signing in...
                </>
              ) : (
                "Login"
              )}
            </Button>
          </Box>

          <Typography
            variant="caption"
            color="text.secondary"
            display="block"
            textAlign="center"
            sx={{ mt: 4 }}
          >
            Authorized personnel only. All activity is logged
            and monitored.
          </Typography>
        </Paper>
      </Container>
    </Box>
  );
}

export default Login;