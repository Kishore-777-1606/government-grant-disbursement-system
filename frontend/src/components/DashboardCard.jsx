import { Card, CardContent, Typography } from "@mui/material";

function DashboardCard({ title, value }) {
  return (
    <Card
      elevation={3}
      sx={{
        borderRadius: 3,
        height: 140,
      }}
    >
      <CardContent>
        <Typography color="text.secondary">
          {title}
        </Typography>

        <Typography
          variant="h3"
          sx={{ mt: 2, fontWeight: "bold" }}
        >
          {value}
        </Typography>
      </CardContent>
    </Card>
  );
}

export default DashboardCard;