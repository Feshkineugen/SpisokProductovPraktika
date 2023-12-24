package com.example.spisok;

import android.os.Environment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private Spinner categorySpinner;
    private EditText priceEditText;
    private ListView productList;
    private ArrayList<Product> products;
    private ArrayAdapter<Product> adapter;
    private int editedPosition = -1;



    public void saveToFile(View view) {
        // Логика сохранения данных в файл
        updateFileContent();
        Toast.makeText(this, "Данные сохранены в файл", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productNameEditText = findViewById(R.id.productNameEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        priceEditText = findViewById(R.id.priceEditText);
        productList = findViewById(R.id.productListView);

        products = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        productList.setAdapter(adapter);

        String[] categories = {"Фрукты", "Овощи", "Молочные продукты","Мясо","Мясо птицы","Рыба","Ягоды","Экзотические фрукты","Грибы","Крупы и злаки" ,"Кондитерские изделия","Напитки","Фастфуд"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        productList.setOnItemClickListener((parent, view, position, id) -> editProduct(position));
    }

    public void addProduct(View view) {
        String productName = productNameEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String priceString = priceEditText.getText().toString();

        if (!productName.trim().isEmpty() && !priceString.trim().isEmpty()) {
            double price = Double.parseDouble(priceString);
            Product product = new Product(productName, category, price);
            products.add(product);
            adapter.notifyDataSetChanged();
            writeToFile(product.toString() + "\n");
            Toast.makeText(this, "Продукт добавлен", Toast.LENGTH_SHORT).show();
            clearInputFields();
        } else {
            Toast.makeText(this, "Введите название продукта и цену", Toast.LENGTH_SHORT).show();
        }
    }




    private void editProduct(final int position) {
        editedPosition = position;

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.edit_product_dialog, null);

        final EditText editProductName = dialogView.findViewById(R.id.editProductName);
        final Spinner editCategorySpinner = dialogView.findViewById(R.id.editCategorySpinner);
        final EditText editPriceEditText = dialogView.findViewById(R.id.editPriceEditText);

        Product productToEdit = products.get(position);
        editProductName.setText(productToEdit.getName());
        editCategorySpinner.setSelection(getIndex(categorySpinner, productToEdit.getCategory()));
        editPriceEditText.setText(String.valueOf(productToEdit.getPrice()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Редактировать продукт")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    try {
                        String editedName = editProductName.getText().toString();
                        String editedCategory = editCategorySpinner.getSelectedItem().toString();
                        String editedPriceString = editPriceEditText.getText().toString();

                        if (!editedName.trim().isEmpty() && !editedPriceString.trim().isEmpty()) {
                            double editedPrice = Double.parseDouble(editedPriceString);
                            Product editedProduct = new Product(editedName, editedCategory, editedPrice);

                            if (editedPosition != -1) {
                                products.set(editedPosition, editedProduct);
                                adapter.notifyDataSetChanged();
                                updateFileContent();
                                Toast.makeText(MainActivity.this, "Продукт отредактирован", Toast.LENGTH_SHORT).show();
                            }
                            clearEditedPosition();
                        } else {
                            Toast.makeText(MainActivity.this, "Введите название продукта и цену", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Ошибка преобразования цены", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> clearEditedPosition())
                .show();
    }





    private void writeToFile(String data) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, "Продукт.txt");
            FileWriter writer = new FileWriter(file, true);
            writer.append(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка записи в файл", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFileContent() {
        try {
            File file = new File(getFilesDir(), "Продукты.txt");
            FileWriter writer = new FileWriter(file, false);
            for (Product product : products) {
                writer.append(product.toString()).append("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка обновления файла", Toast.LENGTH_SHORT).show();

        }
    }

    private void clearInputFields() {
        productNameEditText.setText("");
        priceEditText.setText("");
    }

    private void clearEditedPosition() {
        editedPosition = -1;
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
