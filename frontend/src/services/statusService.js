import api from "./api";

export const getAllApplications = async () => {
    const response = await api.get("/api/v1/applications");
    return response.data;
};