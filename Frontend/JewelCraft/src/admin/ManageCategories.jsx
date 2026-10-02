import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { DashHeader } from '../components/dashboard/StatCard'
import { glyphFor, JewelGlyph } from '../components/JewelImage'
import Modal, { ConfirmModal } from '../components/Modal'
import StatusBadge from '../components/StatusBadge'
import useApi from '../hooks/useApi'
import { applyServerErrors, errorMessage } from '../services/api'
import { categoryService } from '../services/productService'
import { PATTERNS } from '../utils/constants'

function CategoryForm({ category, onSaved, onCancel }) {
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm()

  useEffect(() => {
    reset({ name: category?.name || '', description: category?.description || '', imageUrl: category?.imageUrl || '', active: category ? category.active : true })
  }, [category, reset])

  const submit = async (values) => {
    try {
      const body = { ...values, imageUrl: values.imageUrl || null }
      const saved = category ? await categoryService.update(category.id, body) : await categoryService.create(body)
      toast.success(category ? 'Category updated' : 'Category created')
      onSaved(saved)
    } catch (err) {
      if (!applyServerErrors(err, setError)) toast.error(errorMessage(err))
    }
  }

  return (
    <form onSubmit={handleSubmit(submit)} noValidate>
      <div className="mb-3">
        <label className="form-label" htmlFor="c-name">Name</label>
        <input id="c-name" className={`form-control ${errors.name ? 'is-invalid' : ''}`} {...register('name', { required: 'Category name is required', minLength: { value: 2, message: 'Category name must be 2-80 characters' }, maxLength: { value: 80, message: 'Category name must be 2-80 characters' } })} />
        <div className="invalid-feedback">{errors.name?.message}</div>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="c-desc">Description</label>
        <textarea id="c-desc" rows={3} className="form-control" {...register('description', { maxLength: 500 })} />
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="c-img">Image URL <span className="text-muted-jc">(optional)</span></label>
        <input id="c-img" className={`form-control ${errors.imageUrl ? 'is-invalid' : ''}`} {...register('imageUrl', { validate: (v) => !v || PATTERNS.url.test(v) || 'Must start with http:// or https://' })} />
        <div className="invalid-feedback">{errors.imageUrl?.message}</div>
      </div>
      <div className="form-check form-switch">
        <input id="c-active" type="checkbox" className="form-check-input" {...register('active')} />
        <label htmlFor="c-active" className="form-check-label small">Visible in store</label>
      </div>
      <div className="d-flex justify-content-end gap-2 mt-4">
        <button type="button" className="btn btn-outline-jc btn-sm" onClick={onCancel}>Cancel</button>
        <button className="btn btn-jc btn-sm" disabled={isSubmitting}>{isSubmitting ? 'Saving…' : 'Save'}</button>
      </div>
    </form>
  )
}

/** Category CRUD – store managers and admins. */
export default function ManageCategories() {
  const { data, loading, error, reload } = useApi(() => categoryService.list(true), [])
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)

  const remove = async () => {
    try {
      await categoryService.remove(deleting.id)
      toast.success('Category deleted')
      setDeleting(null)
      reload()
    } catch (err) {
      toast.error(errorMessage(err))
    }
  }

  return (
    <>
      <DashHeader title="Categories" subtitle="Organise the collection shoppers browse.">
        <button className="btn btn-jc btn-sm" onClick={() => setEditing('new')}><i className="bi bi-plus-lg me-1" />Add category</button>
      </DashHeader>
      {loading && !data ? (
        <div className="text-muted-jc">Loading…</div>
      ) : error ? (
        <div className="card-jc p-4" style={{ color: 'var(--jc-bad)' }}>{error}</div>
      ) : (
        <div className="row g-3">
          {data.map((c) => (
            <div key={c.id} className="col-sm-6 col-xl-4">
              <div className="card-jc h-100 p-3 d-flex gap-3">
                <div className="thumb-sm d-flex align-items-center justify-content-center" style={{ width: 64, height: 64, borderRadius: '50%' }}>
                  <JewelGlyph type={glyphFor(c.name)} style={{ width: '70%' }} />
                </div>
                <div className="flex-grow-1 min-w-0">
                  <div className="d-flex justify-content-between align-items-start gap-2">
                    <div className="serif" style={{ fontSize: '1.3rem' }}>{c.name}</div>
                    <StatusBadge status={c.active ? 'ACTIVE' : 'INACTIVE'} label={c.active ? 'Active' : 'Hidden'} />
                  </div>
                  <div className="small text-muted-jc">{c.productCount} products</div>
                  {c.description && <div className="small text-soft mt-1">{c.description}</div>}
                  <div className="d-flex gap-3 mt-2">
                    <button className="btn-link-jc" style={{ fontSize: '0.62rem' }} onClick={() => setEditing(c)}>Edit</button>
                    <button className="btn-link-jc" style={{ fontSize: '0.62rem' }} onClick={() => setDeleting(c)}>Delete</button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      <Modal show={Boolean(editing)} title={editing === 'new' ? 'New category' : 'Edit category'} onClose={() => setEditing(null)}>
        {editing && (
          <CategoryForm
            category={editing === 'new' ? null : editing}
            onCancel={() => setEditing(null)}
            onSaved={() => {
              setEditing(null)
              reload()
            }}
          />
        )}
      </Modal>
      <ConfirmModal
        show={Boolean(deleting)}
        title="Delete category?"
        message={deleting?.productCount ? `“${deleting.name}” still has ${deleting.productCount} products. Move them first, or hide the category instead.` : `“${deleting?.name}” will be deleted.`}
        confirmText="Delete"
        onConfirm={remove}
        onClose={() => setDeleting(null)}
      />
    </>
  )
}
