import cookies from 'js-cookie';
import jwtDecode from 'jwt-decode';

// User
const tokenKey = 'access_token';
export const getTokenDecode = () => jwtDecode(cookies.get(tokenKey) || '');
export const getToken = () => cookies.get(tokenKey);
export const setToken = (token: string) => cookies.set(tokenKey, token);
export const removeToken = () => cookies.remove(tokenKey);

const siteKey = 'site';
export const getSiteUid = () => cookies.get(siteKey);
export const setSiteUid = (siteUid: string) => cookies.set(siteKey, siteUid);
