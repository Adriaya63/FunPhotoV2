package com.example.funphoto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditBioDialog extends AppCompatDialogFragment {

    private EditText editTextBio;
    private Button buttonSave;
    private EditBioDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_bio, null);

        editTextBio = view.findViewById(R.id.editTextBio);
        buttonSave = view.findViewById(R.id.buttonSave);

        builder.setView(view)
                .setTitle("Editar Biografía")
                .setNegativeButton("Cancelar", (dialog, which) -> {})
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newBio = editTextBio.getText().toString();
                    listener.onSaveClicked(newBio);
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EditBioDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement EditBioDialogListener");
        }
    }

    public interface EditBioDialogListener {
        void onSaveClicked(String newBio);
    }
}
