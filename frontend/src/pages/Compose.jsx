import { useState } from 'react'
import apiClient from '../api/apiClient.js'
import MessageBanner from '../components/MessageBanner.jsx'

export default function Compose() {
  const [to, setTo] = useState('')
  const [subject, setSubject] = useState('')
  const [body, setBody] = useState('')
  const [message, setMessage] = useState('')
  const [type, setType] = useState('info')

  async function handleSubmit(e) {
    e.preventDefault()
    setMessage('')
    try {
      const response = await apiClient.post('/emails/send', { to, subject, body })
      setType(response.data.status === 'FAILED' ? 'error' : 'success')
      setMessage(`Email status: ${response.data.status}`)
      setTo('')
      setSubject('')
      setBody('')
    } catch (err) {
      setType('error')
      setMessage(err.response?.data?.message || 'Unable to send email')
    }
  }

  return (
    <div>
      <h1>Compose Email</h1>
      <MessageBanner type={type} message={message} />
      <form className="panel form" onSubmit={handleSubmit}>
        <label>To</label>
        <input value={to} onChange={(e) => setTo(e.target.value)} placeholder="recipient@example.com" />
        <label>Subject</label>
        <input value={subject} onChange={(e) => setSubject(e.target.value)} />
        <label>Message</label>
        <textarea rows="10" value={body} onChange={(e) => setBody(e.target.value)} />
        <button>Send Email</button>
      </form>
    </div>
  )
}
