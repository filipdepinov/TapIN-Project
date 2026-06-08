import {
  Chart as ChartJS, CategoryScale, LinearScale, PointElement,
  LineElement, Tooltip, Filler
} from 'chart.js'
import { Line } from 'react-chartjs-2'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Filler)

export default function TrendChart({ data = [] }) {
  const labels  = data.map(d => {
    const date = new Date(d.date)
    return date.toLocaleDateString('en-GB', { day: 'numeric', month: 'short' })
  })
  const values  = data.map(d => d.count)

  const chartData = {
    labels,
    datasets: [{
      label: 'Attendances',
      data: values,
      fill: true,
      tension: 0.4,
      borderColor: '#2E5FAC',
      borderWidth: 2.5,
      pointBackgroundColor: '#2E5FAC',
      pointRadius: 3,
      pointHoverRadius: 6,
      backgroundColor: (ctx) => {
        const gradient = ctx.chart.ctx.createLinearGradient(0, 0, 0, 220)
        gradient.addColorStop(0, 'rgba(46,95,172,0.18)')
        gradient.addColorStop(1, 'rgba(46,95,172,0)')
        return gradient
      },
    }],
  }

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false }, tooltip: { mode: 'index', intersect: false } },
    scales: {
      x: {
        grid: { display: false },
        ticks: { font: { size: 11 }, color: '#9ca3af', maxTicksLimit: 8 },
      },
      y: {
        grid: { color: 'rgba(0,0,0,0.04)' },
        ticks: { font: { size: 11 }, color: '#9ca3af', precision: 0 },
        beginAtZero: true,
      },
    },
  }

  return (
    <div style={{ height: 220 }}>
      <Line data={chartData} options={options} />
    </div>
  )
}
