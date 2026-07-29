import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#1565C0",
    },
    secondary: {
      main: "#2E7D32",
    },
    background: {
      default: "#F4F7FC",
    },
  },

  typography: {
    fontFamily: "'Poppins', sans-serif",

    h4: {
      fontWeight: 700,
    },

    h6: {
      fontWeight: 600,
    },
  },

  shape: {
    borderRadius: 12,
  },
});

export default theme;