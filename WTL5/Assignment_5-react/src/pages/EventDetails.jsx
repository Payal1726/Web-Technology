import { useParams, Link } from 'react-router-dom'

const eventsData = {
    1: {
        title: 'Tech Workshop 2026',
        date: 'March 15, 2026',
        time: '10:00 AM - 4:00 PM',
        venue: 'Seminar Hall, Block A',
        club: 'Tech Club',
        type: 'Workshop',
        description: 'A hands-on workshop covering the latest web technologies including React, Node.js and cloud deployment. Open to all engineering students.',
        speaker: 'Prof. Rajesh Kumar',
        seats: 100,
    },
    2: {
        title: 'Cultural Fest',
        date: 'March 22, 2026',
        time: '9:00 AM - 8:00 PM',
        venue: 'College Auditorium',
        club: 'Cultural Club',
        type: 'Festival',
        description: 'Annual cultural festival featuring dance, music, drama, and art competitions. Participate and showcase your talent!',
        speaker: 'Various Performers',
        seats: 500,
    },
    3: {
        title: 'AI & ML Seminar',
        date: 'April 5, 2026',
        time: '11:00 AM - 2:00 PM',
        venue: 'Conference Room, Block B',
        club: 'AI Club',
        type: 'Seminar',
        description: 'An insightful seminar on Artificial Intelligence and Machine Learning trends in 2026. Industry experts will share their knowledge.',
        speaker: 'Dr. Sneha Patil',
        seats: 150,
    },
    4: {
        title: 'Coding Competition',
        date: 'April 12, 2026',
        time: '10:00 AM - 1:00 PM',
        venue: 'Computer Lab 3, Block C',
        club: 'Tech Club',
        type: 'Competition',
        description: 'Competitive programming contest with problems ranging from easy to hard. Prizes for top 3 winners!',
        speaker: 'Tech Club Team',
        seats: 60,
    },
    5: {
        title: 'Photography Exhibition',
        date: 'April 18, 2026',
        time: '10:00 AM - 5:00 PM',
        venue: 'Gallery Hall, Block A',
        club: 'Arts Club',
        type: 'Exhibition',
        description: 'An exhibition showcasing the best photographs captured by students. Categories include Nature, Street, and Portrait photography.',
        speaker: 'Arts Club Members',
        seats: 200,
    },
    6: {
        title: 'Debate Championship',
        date: 'April 25, 2026',
        time: '2:00 PM - 5:00 PM',
        venue: 'Seminar Hall, Block B',
        club: 'Literary Club',
        type: 'Competition',
        description: 'Inter-college debate championship on current affairs topics. Teams of 2 can participate.',
        speaker: 'Prof. Meena Sharma',
        seats: 80,
    },
}

function EventDetails() {
    const { id } = useParams()
    const event = eventsData[id]

    if (!event) {
        return (
            <div className="page">
                <div className="success-card">
                    <div className="success-icon">❌</div>
                    <h2 style={{ color: '#c62828' }}>Event Not Found</h2>
                    <p>The event you are looking for does not exist.</p>
                    <Link to="/events" className="link-back">← Back to Events</Link>
                </div>
            </div>
        )
    }

    return (
        <div className="page">
            <div className="page-header">
                <h2>📌 {event.title}</h2>
            </div>

            <table className="detail-table">
                <tbody>
                    <tr><td>📅 Date</td><td>{event.date}</td></tr>
                    <tr><td>🕐 Time</td><td>{event.time}</td></tr>
                    <tr><td>📍 Venue</td><td>{event.venue}</td></tr>
                    <tr><td>📂 Type</td><td>{event.type}</td></tr>
                    <tr><td>🏛️ Organized By</td><td>{event.club}</td></tr>
                    <tr><td>🎤 Speaker / Host</td><td>{event.speaker}</td></tr>
                    <tr><td>💺 Available Seats</td><td>{event.seats}</td></tr>
                </tbody>
            </table>

            <div className="about-section" style={{ maxWidth: '600px' }}>
                <h3>Description</h3>
                <p>{event.description}</p>
            </div>

            <div style={{ display: 'flex', gap: '12px', marginTop: '20px', flexWrap: 'wrap' }}>
                <Link to={`/register/${id}`} className="btn btn-accent">Register for this Event</Link>
                <Link to="/events" className="btn btn-outline">← Back to Events</Link>
            </div>
        </div>
    )
}

export default EventDetails
