import { useEffect, useState } from 'react'
import apiClient from '../api/apiClient.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function Dashboard() {
  const { user } = useAuth()
  const [sentCount, setSentCount] = useState(0)
  const [inboxCount, setInboxCount] = useState(0)

  useEffect(() => {
    async function load() {
      const [sent, inbox] = await Promise.all([
        apiClient.get('/emails/sent'),
        apiClient.get('/emails/inbox')
      ])
      setSentCount(sent.data.length)
      setInboxCount(inbox.data.length)
    }
    load()
  }, [])

  return (
    <div>
      <h1>Dashboard</h1>
      <p className="muted">Welcome, {user?.fullName}. Manage secure email activity from one place.</p>
      <div className="stats-grid">
        <div className="stat-card">
          <span>Sent Emails</span>
          <strong>{sentCount}</strong>
        </div>
        <div className="stat-card">
          <span>Inbox Emails</span>
          <strong>{inboxCount}</strong>
        </div>
        <div className="stat-card">
          <span>Role</span>
          <strong>{user?.role}</strong>
        </div>
      </div>
      <section className="panel">
        <h2>Portfolio Highlights</h2>
        <ul>
          <li>Spring Boot REST APIs secured with JWT</li>
          <li>Database-backed sent and received email history</li>
          <li>Role-based admin module</li>
          <li>React frontend integrated with protected APIs</li>
        </ul>
      </section>
    </div>
  )
}
