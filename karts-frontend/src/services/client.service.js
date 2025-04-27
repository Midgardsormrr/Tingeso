import httpClient from "../http-common";

const getAll = () => {
    return httpClient
      .get("/clients")
      .then(res => {
        console.log("Respuesta de la API:", res);
        return res.data; // Cambiar esto (quitar .data extra)
      });
};

const create = (data) => {
  return httpClient.post('/clients', data);
};

const getByRut = (rut) => {
  return httpClient.get(`/clients/${rut}`);
};

const update = (data) => {
  return httpClient.put('/clients', data);
};

const remove = (rut) => {
  return httpClient.delete(`/clients/${rut}`);
};

export default {
  getAll,
  create,
  getByRut,
  update,
  remove
};
