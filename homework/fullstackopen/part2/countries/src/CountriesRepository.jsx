import axios from "axios";

const BASE_URL = "https://studies.cs.helsinki.fi/restcountries/api";

const extractResp = (resp) => resp.data;

const listCountries = () => {
  return axios.get(BASE_URL + "/all").then(extractResp);
};

const getCountry = (name) => {
  return axios.post(`${BASE_URL}/name/${name}`).then(extractResp);
};

export default { listCountries, getCountry };
