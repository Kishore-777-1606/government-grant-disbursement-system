import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";

import App from "./App";
import theme from "./theme";
import { AuthProvider } from "./auth/AuthProvider";

ReactDOM.createRoot(
  document.getElementById("root")
).render(
  <ThemeProvider theme={theme}>
    <CssBaseline />

    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </ThemeProvider>
);