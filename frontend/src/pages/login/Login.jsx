import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Alert,
  Paper,
  TextField,
  Typography,
  CircularProgress,
  InputAdornment,
} from "@mui/material";
import {
  AccountBalance as AccountBalanceIcon,
  Person as PersonOutlineIcon,
  Lock as LockOutlinedIcon,
} from "@mui/icons-material";
import { login } from "../../services/authService";

function Login() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!username.trim() || !password) {
      setError("Please enter both username and password.");
      return;
    }

    setLoading(true);
    try {
      const user = await login(username.trim(), password);
      localStorage.setItem("user", JSON.stringify(user));
      navigate("/dashboard");
    } catch (err) {
      // Backend returns { message } for both 404 (bad credentials) and
      // 400 (disabled account); anything else falls back to a generic message.
      const message =
        err.response?.data?.message ||
        "Unable to log in. Please try again.";
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
          "linear-gradient(135deg, #0D47A1 0%, #1565C0 50%, #1976D2 100%)",
        px: 2,
      }}
    >
      <Paper
        elevation={6}
        sx={{
          width: "100%",
          maxWidth: 420,
          p: { xs: 3, sm: 5 },
          borderRadius: 3,
        }}
      >
        {/* Brand header */}
        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", mb: 4 }}>
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              background: "#E3F2FD",
              mb: 1.5,
            }}
          >
            <AccountBalanceIcon sx={{ color: "#1565C0", fontSize: 30 }} />
          </Box>
          <Typography variant="h5" fontWeight={700} textAlign="center">
            GrantFlow
          </Typography>
          <Typography variant="body2" color="text.secondary" textAlign="center">
            Government Grant Disbursement Tracking System
          </Typography>
        </Box>

        <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 2 }}>
          Sign in to your account
        </Typography>

        <Box component="form" onSubmit={handleSubmit} noValidate>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <TextField
            label="Username"
            fullWidth
            margin="normal"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            disabled={loading}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <PersonOutlineIcon color="action" />
                </InputAdornment>
              ),
            }}
          />

          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={loading}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <LockOutlinedIcon color="action" />
                </InputAdornment>
              ),
            }}
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            size="large"
            sx={{ mt: 3, py: 1.3, fontWeight: 600 }}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : "Login"}
          </Button>
        </Box>

        <Typography
          variant="caption"
          color="text.secondary"
          display="block"
          textAlign="center"
          sx={{ mt: 4 }}
        >
          Authorized personnel only. All activity is logged and monitored.
        </Typography>
      </Paper>
    </Box>
  );
}

export default Login;