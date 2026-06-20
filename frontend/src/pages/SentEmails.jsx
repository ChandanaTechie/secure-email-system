import { useEffect, useState } from 'react'
import apiClient from '../api/apiClient.js'

export default function SentEmails() {
  const [emails, setEmails] = useState([])

  useEffect(() => {
    apiClient.get('/emails/sent').then((response) => setEmails(response.data))
  }, [])

  return (
    <div>
      <h1>Sent Emails</h1>
      <EmailTable emails={emails} empty="No sent emails yet." />
    </div>
  )
}

function EmailTable({ emails, empty }) {
  if (emails.length === 0) return <p className="muted">{empty}</p>
  return (
    <div className="table-card">
      <table>
        <thead>
          <tr>
            <th>To</th>
            <th>Subject</th>
            <th>Status</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {emails.map((email) => (
            <tr key={email.id}>
              <td>{email.receiverEmail}</td>
              <td>{email.subject}</td>
              <td><span className={`status ${email.status}`}>{email.status}</span></td>
              <td>{new Date(email.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
