import api from "./api";

export const getAllApplications = async () => {
    const response = await api.get("/api/v1/applications");
    return response.data;
};

export const createApplication = async (application) => {
    const response = await api.post(
        "/api/v1/applications",
        application
    );
    return response.data;
};

export const deleteApplication = async (id) => {
    await api.delete(`/api/v1/applications/${id}`);
};

export const updateApplication = async (id, application) => {
    const response = await api.put(
        `/api/v1/applications/${id}`,
        application
    );
    return response.data;
};