import { useParams, Link } from 'react-router-dom'
import { useState } from 'react'

const eventNames = {
    1: 'Tech Workshop 2026',
    2: 'Cultural Fest',
    3: 'AI & ML Seminar',
    4: 'Coding Competition',
    5: 'Photography Exhibition',
    6: 'Debate Championship',
}

function Register() {
    const { id } = useParams()
    const eventName = eventNames[id] || 'Unknown Event'
    const [submitted, setSubmitted] = useState(false)
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        department: '',
        year: '',
    })

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value })
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        setSubmitted(true)
    }

    if (submitted) {
        return (
            <div className="page">
                <div className="success-card">
                    <div className="success-icon">✅</div>
                    <h2>Registration Successful!</h2>
                    <p><strong>{formData.name}</strong>, you have been registered for <strong>{eventName}</strong>.</p>
                    <p>A confirmation will be sent to <strong>{formData.email}</strong>.</p>
                    <br />
                    <Link to="/events" className="btn btn-primary">← Back to Events</Link>
                </div>
            </div>
        )
    }

    return (
        <div className="page">
            <div className="page-header">
                <h2>📝 Register for: {eventName}</h2>
            </div>

            <div className="form-container">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Full Name</label>
                        <input type="text" name="name" value={formData.name} onChange={handleChange} required placeholder="Enter your full name" />
                    </div>
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" name="email" value={formData.email} onChange={handleChange} required placeholder="Enter your email" />
                    </div>
                    <div className="form-group">
                        <label>Phone</label>
                        <input type="tel" name="phone" value={formData.phone} onChange={handleChange} required placeholder="Enter your phone number" />
                    </div>
                    <div className="form-group">
                        <label>Department</label>
                        <select name="department" value={formData.department} onChange={handleChange} required>
                            <option value="">Select Department</option>
                            <option value="Computer">Computer Engineering</option>
                            <option value="IT">Information Technology</option>
                            <option value="ENTC">ENTC</option>
                            <option value="Mechanical">Mechanical Engineering</option>
                            <option value="Civil">Civil Engineering</option>
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Year</label>
                        <select name="year" value={formData.year} onChange={handleChange} required>
                            <option value="">Select Year</option>
                            <option value="FE">FE</option>
                            <option value="SE">SE</option>
                            <option value="TE">TE</option>
                            <option value="BE">BE</option>
                        </select>
                    </div>
                    <button type="submit" className="form-submit">Register Now</button>
                </form>
            </div>

            <Link to={`/events/${id}`} className="link-back">← Back to Event Details</Link>
        </div>
    )
}

export default Register
