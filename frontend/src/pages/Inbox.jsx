import { useEffect, useState } from 'react'
import apiClient from '../api/apiClient.js'
import MessageBanner from '../components/MessageBanner.jsx'

export default function Inbox() {
  const [emails, setEmails] = useState([])
  const [message, setMessage] = useState('')

  async function loadInbox() {
    const response = await apiClient.get('/emails/inbox')
    setEmails(response.data)
  }

  async function syncInbox() {
    setMessage('')
    const response = await apiClient.post('/emails/inbox/sync')
    await loadInbox()
    if (response.data.length === 0) {
      setMessage('Inbox sync is disabled or mail credentials are not configured.')
    } else {
      setMessage(`Synced ${response.data.length} email record(s).`)
    }
  }

  useEffect(() => {
    loadInbox()
  }, [])

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Inbox</h1>
          <p className="muted">Use Sync Inbox after configuring IMAP settings.</p>
        </div>
        <button onClick={syncInbox}>Sync Inbox</button>
      </div>
      <MessageBanner type="info" message={message} />
      {emails.length === 0 ? <p className="muted">No inbox emails stored yet.</p> : (
        <div className="email-list">
          {emails.map((email) => (
            <article className="email-card" key={email.id}>
              <div className="email-card-header">
                <strong>{email.subject}</strong>
                <span className={`status ${email.status}`}>{email.status}</span>
              </div>
              <p>From: {email.senderEmail}</p>
              <p>{email.body?.slice(0, 250)}</p>
            </article>
          ))}
        </div>
      )}
    </div>
  )
}
