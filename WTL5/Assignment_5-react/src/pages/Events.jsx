import { Link } from 'react-router-dom'

const allEvents = [
    { id: 1, title: 'Tech Workshop 2026', date: 'March 15, 2026', type: 'Workshop', club: 'Tech Club' },
    { id: 2, title: 'Cultural Fest', date: 'March 22, 2026', type: 'Festival', club: 'Cultural Club' },
    { id: 3, title: 'AI & ML Seminar', date: 'April 5, 2026', type: 'Seminar', club: 'AI Club' },
    { id: 4, title: 'Coding Competition', date: 'April 12, 2026', type: 'Competition', club: 'Tech Club' },
    { id: 5, title: 'Photography Exhibition', date: 'April 18, 2026', type: 'Exhibition', club: 'Arts Club' },
    { id: 6, title: 'Debate Championship', date: 'April 25, 2026', type: 'Competition', club: 'Literary Club' },
]

const badgeClass = {
    Workshop: 'badge-workshop',
    Festival: 'badge-festival',
    Seminar: 'badge-seminar',
    Competition: 'badge-competition',
    Exhibition: 'badge-exhibition',
}

function Events() {
    return (
        <div className="page">
            <div className="page-header">
                <h2>📋 All Events</h2>
                <p>Browse through all upcoming college events below.</p>
            </div>

            <table className="styled-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Event Name</th>
                        <th>Date</th>
                        <th>Type</th>
                        <th>Organized By</th>
                        <th>Details</th>
                        <th>Register</th>
                    </tr>
                </thead>
                <tbody>
                    {allEvents.map((event, index) => (
                        <tr key={event.id}>
                            <td>{index + 1}</td>
                            <td><strong>{event.title}</strong></td>
                            <td>{event.date}</td>
                            <td><span className={`badge ${badgeClass[event.type] || ''}`}>{event.type}</span></td>
                            <td>{event.club}</td>
                            <td><Link to={`/events/${event.id}`} className="btn btn-primary" style={{ padding: '5px 14px', fontSize: '0.82rem' }}>View</Link></td>
                            <td><Link to={`/register/${event.id}`} className="btn btn-accent" style={{ padding: '5px 14px', fontSize: '0.82rem' }}>Register</Link></td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

export default Events
