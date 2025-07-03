// src/api/auth.js
import axios from 'axios'

export const checkAuth = async () => {
  try {
    const response = await axios.get('/api/auth/check', {withCredentials: true}) // 토큰은 쿠키로 자동 전송됨
    return response.data.authenticated === true
  } catch (error) {
    return false
  }
}