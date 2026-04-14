import { Link } from 'react-router-dom'

const clubs = [
    {
        name: 'Tech Club',
        description: 'Focuses on coding, web development, and new technologies. Organizes workshops, hackathons and coding contests.',
        members: 120,
        events: ['Tech Workshop 2026', 'Coding Competition'],
    },
    {
        name: 'Cultural Club',
        description: 'Promotes arts, dance, music and drama. Organizes the annual cultural festival and talent shows.',
        members: 200,
        events: ['Cultural Fest'],
    },
    {
        name: 'AI Club',
        description: 'Dedicated to Artificial Intelligence and Machine Learning. Conducts seminars, paper presentations and research discussions.',
        members: 80,
        events: ['AI & ML Seminar'],
    },
    {
        name: 'Arts Club',
        description: 'For students passionate about photography, painting and creative arts. Organizes exhibitions and art competitions.',
        members: 90,
        events: ['Photography Exhibition'],
    },
    {
        name: 'Literary Club',
        description: 'Promotes reading, writing, public speaking and debate. Hosts debate championships and poetry slams.',
        members: 60,
        events: ['Debate Championship'],
    },
]

function Clubs() {
    return (
        <div className="page">
            <div className="page-header">
                <h2>🏛️ College Clubs</h2>
                <p>Explore the different clubs and their activities.</p>
            </div>

            {clubs.map((club, index) => (
                <div key={index} className="card">
                    <h3>{club.name}</h3>
                    <p>{club.description}</p>
                    <div className="club-stats">
                        <span className="stat">👥 {club.members} Members</span>
                    </div>
                    <div className="event-tags">
                        {club.events.map((ev, i) => (
                            <span key={i} className="event-tag">📅 {ev}</span>
                        ))}
                    </div>
                </div>
            ))}

            <Link to="/events" className="btn btn-outline">View All Events →</Link>
        </div>
    )
}

export default Clubs
