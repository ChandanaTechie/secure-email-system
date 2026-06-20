import { useEffect, useState } from 'react'
import apiClient from '../api/apiClient.js'

export default function AdminUsers() {
  const [users, setUsers] = useState([])

  async function loadUsers() {
    const response = await apiClient.get('/admin/users')
    setUsers(response.data)
  }

  async function updateStatus(userId, enabled) {
    await apiClient.put(`/admin/users/${userId}/status`, { enabled })
    await loadUsers()
  }

  useEffect(() => {
    loadUsers()
  }, [])

  return (
    <div>
      <h1>Admin Users</h1>
      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.fullName}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>{user.enabled ? 'Enabled' : 'Disabled'}</td>
                <td>
                  {user.role !== 'ADMIN' && (
                    <button className="small" onClick={() => updateStatus(user.id, !user.enabled)}>
                      {user.enabled ? 'Disable' : 'Enable'}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
