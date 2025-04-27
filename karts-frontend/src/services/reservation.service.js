import httpClient from "../http-common";

const getAll = () => {
  return httpClient.get("/reservations")
    .then(res => res.data);
};

const getById = (id) => {
  return httpClient.get(`/reservations/${id}`)
    .then(res => res.data);
};

const create = (reservationData) => {
  return httpClient.post("/reservations/create", reservationData)
    .then(res => res.data);
};

const update = (reservationData) => {
  return httpClient.put("/reservations", reservationData)
    .then(res => res.data);
};

const remove = (id) => {
  return httpClient.delete(`/reservations/${id}`)
    .then(res => res.data);
};

export default {
  getAll,
  getById,
  create,
  update,
  remove
};