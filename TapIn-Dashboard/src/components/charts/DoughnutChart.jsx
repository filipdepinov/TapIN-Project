import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'

ChartJS.register(ArcElement, Tooltip, Legend)

export default function DoughnutChart({ data = [] }) {
  const above80  = data.filter(s => s.attendanceRate >= 80).length
  const mid60    = data.filter(s => s.attendanceRate >= 60 && s.attendanceRate < 80).length
  const below60  = data.filter(s => s.attendanceRate < 60).length

  const chartData = {
    labels: ['≥ 80% (Good)', '60–79% (Fair)', '< 60% (At Risk)'],
    datasets: [{
      data: [above80, mid60, below60],
      backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
      borderColor: ['#fff', '#fff', '#fff'],
      borderWidth: 3,
      hoverOffset: 6,
    }],
  }

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '68%',
    plugins: {
      legend: { position: 'bottom', labels: { font: { size: 11 }, padding: 14, boxWidth: 12 } },
      tooltip: { callbacks: { label: (c) => ` ${c.label}: ${c.raw} students` } },
    },
  }

  return (
    <div style={{ height: 220 }}>
      <Doughnut data={chartData} options={options} />
    </div>
  )
}
