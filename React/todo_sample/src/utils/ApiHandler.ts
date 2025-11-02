import axios from 'axios';

export const ApiHandler = axios.create({
  baseURL: process.env.REACT_API_URL,
});
