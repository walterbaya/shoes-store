// Ejemplo: Confirmación antes de eliminar
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', (e) => {
            if (!confirm('¿Estás seguro de eliminar este elemento?')) {
                e.preventDefault();
            }
        });
    });
});