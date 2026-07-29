import api from "./api";

export const getAllSchemes = async () => {
    const response = await api.get("/schemes");
    return response.data;
};

export const getSchemeById = async (id) => {
    const response = await api.get(`/schemes/${id}`);
    return response.data;
};

export const createScheme = async (scheme) => {
    const response = await api.post("/schemes", scheme);
    return response.data;
};

export const updateScheme = async (id, scheme) => {
    const response = await api.put(`/schemes/${id}`, scheme);
    return response.data;
};

export const deleteScheme = async (id) => {
    await api.delete(`/schemes/${id}`);
};
