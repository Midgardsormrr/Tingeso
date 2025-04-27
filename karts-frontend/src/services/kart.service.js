// src/services/kart.service.js
import httpClient from "../http-common";
const getAll = () => httpClient.get("/karts").then(res=>res.data);
const getById = id => httpClient.get(`/karts/${id}`).then(res=>res.data);
const create = kart => httpClient.post("/karts", kart).then(res=>res.data);
const updateStatus = (id, status) => httpClient.put(`/karts/${id}/status?status=${status}`).then(res => res.data);
const remove = id => httpClient.delete(`/karts/${id}`).then(res => res.data);
export default { getAll, getById, create, updateStatus, remove };