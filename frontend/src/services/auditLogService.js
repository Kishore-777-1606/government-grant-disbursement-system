import api from "./api";

export const getAuditLogs = async (entityType, entityId) => {
    const params = {};
    if (entityType) params.entityType = entityType;
    if (entityId) params.entityId = entityId;

    const response = await api.get("/audit-logs", { params });
    return response.data;
};