import { Link } from 'react-router-dom'

function Navbar() {
    return (
        <nav className="navbar">
            <div className="navbar-brand">
                🎓 <span>EventHub</span>
            </div>
            <div className="navbar-links">
                <Link to="/">Home</Link>
                <Link to="/events">Events</Link>
                <Link to="/clubs">Clubs</Link>
                <Link to="/about">About</Link>
            </div>
        </nav>
    )
}

export default Navbar
