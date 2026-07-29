import api from "./api";

export const getAllBeneficiaries = async () => {
    const response = await api.get("/beneficiaries");
    return response.data;
};

export const createBeneficiary = async (beneficiary) => {
    const response = await api.post("/beneficiaries", beneficiary);
    return response.data;
};
