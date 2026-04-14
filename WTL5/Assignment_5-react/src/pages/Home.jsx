import { Link } from 'react-router-dom'

const upcomingEvents = [
    { id: 1, title: 'Tech Workshop 2026', date: 'March 15, 2026', club: 'Tech Club' },
    { id: 2, title: 'Cultural Fest', date: 'March 22, 2026', club: 'Cultural Club' },
    { id: 3, title: 'AI & ML Seminar', date: 'April 5, 2026', club: 'AI Club' },
]

function Home() {
    return (
        <div className="page">
            <div className="hero">
                <h2>Welcome to College Event Management System</h2>
                <p>Stay updated with all upcoming college events, workshops, seminars, and club activities.</p>
            </div>

            <h3 className="section-title">📅 Upcoming Events</h3>
            <table className="styled-table">
                <thead>
                    <tr>
                        <th>Event</th>
                        <th>Date</th>
                        <th>Club</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {upcomingEvents.map(event => (
                        <tr key={event.id}>
                            <td>{event.title}</td>
                            <td>{event.date}</td>
                            <td>{event.club}</td>
                            <td><Link to={`/events/${event.id}`} className="btn btn-primary" style={{ padding: '6px 16px', fontSize: '0.85rem' }}>View Details</Link></td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <Link to="/events" className="btn btn-outline">Browse All Events →</Link>
        </div>
    )
}

export default Home
