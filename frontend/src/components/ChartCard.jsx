import { Paper, Typography } from "@mui/material";

function ChartCard({ title, children }) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 3,
        borderRadius: 4,
        border: "1px solid #EDF0F5",
        minHeight: 420,
        transition: "0.25s",
        "&:hover": {
          boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
          transform: "translateY(-3px)",
        },
      }}
    >
      <Typography
        variant="h6"
        fontWeight={700}
        mb={2}
        sx={{ color: "#1E293B" }}
      >
        {title}
      </Typography>

      {children}
    </Paper>
  );
}

export default ChartCard;