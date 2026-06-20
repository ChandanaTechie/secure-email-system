import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import MessageBanner from '../components/MessageBanner.jsx'

export default function Login() {
  const [email, setEmail] = useState('user@example.com')
  const [password, setPassword] = useState('user123')
  const [error, setError] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      await login(email, password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed')
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>SecureMail Login</h1>
        <p>Demo user: user@example.com / user123</p>
        <MessageBanner type="error" message={error} />
        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} />
        <label>Password</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button>Login</button>
        <p>New user? <Link to="/register">Register</Link></p>
      </form>
    </div>
  )
}
