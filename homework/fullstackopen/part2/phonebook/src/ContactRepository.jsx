import axios from "axios";

const BASE_URL = "http://localhost:3001/persons";

const extractResp = (resp) => resp.data;

const listContacts = () => {
  return axios.get(BASE_URL).then(extractResp);
};

const createContact = (name, number) => {
  return axios.post(BASE_URL, { name, number }).then(extractResp);
};

const deleteContact = (id) => {
  return axios.delete(`${BASE_URL}/${id}`).then(extractResp);
};

const updateContact = (id, data) => {
  return axios.patch(`${BASE_URL}/${id}`, data).then(extractResp);
};

export default { listContacts, createContact, deleteContact, updateContact };
