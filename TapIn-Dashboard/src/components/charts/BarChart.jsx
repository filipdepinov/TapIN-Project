import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend
} from 'chart.js'
import { Bar } from 'react-chartjs-2'

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend)

export default function BarChart({ data = [] }) {
  const labels   = data.map(d => d.courseCode)
  const attended = data.map(d => d.totalAttendances)
  const enrolled = data.map(d => d.enrolledStudents * (d.totalSessions || 1))

  const chartData = {
    labels,
    datasets: [
      {
        label: 'Attendances',
        data: attended,
        backgroundColor: '#2E5FAC',
        borderRadius: 6,
        borderSkipped: false,
      },
      {
        label: 'Max Possible',
        data: enrolled,
        backgroundColor: 'rgba(46,95,172,0.12)',
        borderRadius: 6,
        borderSkipped: false,
      },
    ],
  }

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: true, position: 'top', labels: { font: { size: 11 }, boxWidth: 12 } },
      tooltip: { mode: 'index', intersect: false },
    },
    scales: {
      x: { grid: { display: false }, ticks: { font: { size: 11 }, color: '#9ca3af' } },
      y: { grid: { color: 'rgba(0,0,0,0.04)' }, ticks: { font: { size: 11 }, color: '#9ca3af', precision: 0 }, beginAtZero: true },
    },
  }

  return (
    <div style={{ height: 220 }}>
      <Bar data={chartData} options={options} />
    </div>
  )
}
