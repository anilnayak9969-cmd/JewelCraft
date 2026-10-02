import { DEPARTMENTS, GENDERS, VEHICLE_TYPES } from '../utils/constants'
import { formatDate, metalLabel } from '../utils/format'

const label = (list, value) => list.find((x) => x.value === value)?.label || value

/** Builds [label, value] rows from the role-specific details given at sign-up. */
function rowsFor(role, p) {
  if (!p) return []
  switch (role) {
    case 'CUSTOMER':
      return [
        ['Gender', p.gender && label(GENDERS, p.gender)],
        ['Birthday', p.dateOfBirth && formatDate(p.dateOfBirth)],
        ['Anniversary', p.anniversaryDate && formatDate(p.anniversaryDate)],
        ['Favourite metal', p.preferredMetal && metalLabel(p.preferredMetal)],
        ['Occasion offers', p.marketingOptIn == null ? null : p.marketingOptIn ? 'Yes' : 'No'],
      ]
    case 'STAFF':
    case 'STORE_MANAGER':
      return [
        ['Employee code', p.employeeCode],
        ['Department', p.department && label(DEPARTMENTS, p.department)],
        ['Designation', p.designation],
        ['Store branch', p.storeBranch],
        ['Experience', p.experienceYears != null && `${p.experienceYears} year${p.experienceYears === 1 ? '' : 's'}`],
        ['Qualification', p.qualification],
        ['Previous employer', p.previousEmployer],
      ]
    case 'ADMIN':
      return [
        ['Employee code', p.employeeCode],
        ['Designation', p.designation],
        ['Office', p.storeBranch],
        ['Experience', p.experienceYears != null && `${p.experienceYears} year${p.experienceYears === 1 ? '' : 's'}`],
        ['Qualification', p.qualification],
        ['Previous employer', p.previousEmployer],
      ]
    case 'DELIVERY_PARTNER':
      return [
        ['Vehicle', p.vehicleType && label(VEHICLE_TYPES, p.vehicleType)],
        ['Vehicle number', p.vehicleNumber],
        ['Driving licence', p.drivingLicenseNumber],
        ['Service area', p.serviceCity && `${p.serviceCity} – ${p.servicePincode}`],
        ['Emergency contact', p.emergencyContactName && `${p.emergencyContactName}${p.emergencyContactPhone ? ` · ${p.emergencyContactPhone}` : ''}`],
      ]
    default:
      return []
  }
}

/** Read-only table of sign-up details; renders nothing when there are none. */
export default function RoleDetails({ role, profile, title }) {
  const rows = rowsFor(role, profile).filter(([, v]) => v)
  if (rows.length === 0) return null
  return (
    <div>
      {title && <h6 className="small-caps mb-2">{title}</h6>}
      <table className="w-100 spec-table">
        <tbody>
          {rows.map(([k, v]) => (
            <tr key={k}><td>{k}</td><td>{v}</td></tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
