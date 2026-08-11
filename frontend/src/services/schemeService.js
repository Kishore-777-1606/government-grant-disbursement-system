import api from "./api";

export const getAllSchemes = async () => {
    const response = await api.get("/api/schemes");
    return response.data;
};

export const getSchemeById = async (id) => {
    const response = await api.get(`/api/schemes/${id}`);
    return response.data;
};

export const createScheme = async (scheme) => {
    const response = await api.post("/api/schemes", scheme);
    return response.data;
};

export const updateScheme = async (id, scheme) => {
    const response = await api.put(`/api/schemes/${id}`, scheme);
    return response.data;
};

export const deleteScheme = async (id) => {
    await api.delete(`/api/schemes/${id}`);
};