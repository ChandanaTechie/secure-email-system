import { useEffect, useState } from 'react'
import apiClient from '../api/apiClient.js'

export default function AdminLogs() {
  const [logs, setLogs] = useState([])

  useEffect(() => {
    apiClient.get('/admin/email-logs').then((response) => setLogs(response.data))
  }, [])

  return (
    <div>
      <h1>Email Activity Logs</h1>
      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>Owner</th>
              <th>Direction</th>
              <th>From</th>
              <th>To</th>
              <th>Subject</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.ownerEmail}</td>
                <td>{log.direction}</td>
                <td>{log.senderEmail}</td>
                <td>{log.receiverEmail}</td>
                <td>{log.subject}</td>
                <td><span className={`status ${log.status}`}>{log.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
