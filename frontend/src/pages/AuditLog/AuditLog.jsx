import { useEffect, useState } from "react";
import {
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    CircularProgress,
    Alert
} from "@mui/material";

import MainLayout from "../../layouts/MainLayout";
import { getAuditLogs } from "../../services/auditLogService";

function AuditLog() {

    const stored = localStorage.getItem("user");
    const currentRole = stored ? JSON.parse(stored)?.role : null;
    const allowed = currentRole === "ADMIN" || currentRole === "DISTRICT_OFFICER";

    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!allowed) {
            setLoading(false);
            return;
        }
        loadLogs();
    }, []);

    const loadLogs = async () => {

        setLoading(true);
        setError(null);

        try {

            const data = await getAuditLogs();

            const sorted = [...data].sort(
                (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
            );

            setLogs(sorted);

        } catch (err) {

            console.error(err);
            setError(
                err?.response?.data?.message ||
                "Failed to load audit logs."
            );

        } finally {

            setLoading(false);

        }

    };

    if (!allowed) {
        return (
            <MainLayout>
                <Typography variant="h4" gutterBottom>
                    Audit Log
                </Typography>
                <Alert severity="error">
                    You do not have permission to view this page.
                </Alert>
            </MainLayout>
        );
    }

    return (

        <MainLayout>

            <Typography variant="h4" gutterBottom>
                Audit Log
            </Typography>

            {loading ? (

                <CircularProgress />

            ) : error ? (

                <Alert severity="error">{error}</Alert>

            ) : logs.length === 0 ? (

                <Alert severity="info">No audit log entries yet.</Alert>

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>
                                <TableCell><b>Timestamp</b></TableCell>
                                <TableCell><b>Performed By</b></TableCell>
                                <TableCell><b>Role</b></TableCell>
                                <TableCell><b>Action</b></TableCell>
                                <TableCell><b>Entity</b></TableCell>
                                <TableCell><b>Details</b></TableCell>
                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {logs.map((log) => (

                                <TableRow key={log.logId} hover>

                                    <TableCell>
                                        {log.timestamp
                                            ? new Date(log.timestamp).toLocaleString()
                                            : "-"}
                                    </TableCell>

                                    <TableCell>
                                        {log.performedBy || "-"}
                                    </TableCell>

                                    <TableCell>
                                        {log.actorRole || "-"}
                                    </TableCell>

                                    <TableCell>
                                        {log.actionType || "-"}
                                    </TableCell>

                                    <TableCell>
                                        {log.entityType
                                            ? `${log.entityType} #${log.entityId ?? "-"}`
                                            : "-"}
                                    </TableCell>

                                    <TableCell>
                                        {log.details || "-"}
                                    </TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

        </MainLayout>

    );

}

export default AuditLog;