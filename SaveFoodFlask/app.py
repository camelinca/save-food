from flask import Flask, request, jsonify
import firebase_admin
import pandas as pd
from firebase_admin import credentials, firestore
from firebase_admin import db

app = Flask(__name__)

# Inisialisasi Firebase

cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://savefood-3ff05-default-rtdb.firebaseio.com/'
})

@app.route('/get_menu_dataframe', methods=['GET'])
def get_menu_dataframe():

        menu_ref = db.reference('menu').get()
        df_menu = pd.DataFrame(menu_ref).T
        hapus_kolom = ['foto', 'deskripsi', 'harga', 'diskon', 'stok','id_merchant']
        df_menu= df_menu.drop(columns=hapus_kolom)
        return df_menu

@app.route('/get_pesanan_dataframe', methods=['GET'])
def get_pesanan_dataframe():
        pesanan_data = db.reference('pesanan').get()
        detail_pesanan_data = db.reference('detail_pesanan').get()
        df_pesanan = pd.DataFrame(pesanan_data)
        df_detail_pesanan = pd.DataFrame(detail_pesanan_data)
        df_gabung = pd.merge(df_pesanan.T, df_detail_pesanan.T, left_on='id_order', right_on='id_order', how='inner')
        hapus_kolom = ['id_order', 'id_detail', 'jumlah', 'tanggal_pesanan', 'total_harga_x','total_harga_y', 'status','id_merchant']
        df_gabung = df_gabung.drop(columns=hapus_kolom)
        return df_gabung
    
def content_based_filtering(id_konsumen, df_pesanan, df_menu):
    df_pesanan = pd.merge(df_pesanan, df_menu, on='id_menu', how='left')
    df_konsumen = df_pesanan[df_pesanan['id_konsumen'] == id_konsumen]
    frekuensi_kategori = df_konsumen['id_kategori'].value_counts()
    total_frekuensi = frekuensi_kategori.sum()
    normalisasi_kategori = frekuensi_kategori / total_frekuensi
    list_nilai_normalisasi = []

    for index, row in df_menu.iterrows():
        id_menu = row['id_menu']
        nama = row['nama']
        kategori_menu = row['id_kategori']

        if kategori_menu in normalisasi_kategori.index:
            nilai_normalisasi = normalisasi_kategori[kategori_menu]
        else:
             nilai_normalisasi = 0
        list_nilai_normalisasi.append({'id_menu': id_menu,'nilai_normalisasi': nilai_normalisasi,'nilai_normalisasi': nilai_normalisasi})
        sorted_list_nilai_normalisasi = sorted(list_nilai_normalisasi, key=lambda x: x['nilai_normalisasi'], reverse=True)

    return sorted_list_nilai_normalisasi

@app.route('/get_recommendations', methods=['POST'])
def get_recommendations():
    data = request.get_json()
    id_konsumen_tertentu = data['id_konsumen']
    df_menu = get_menu_dataframe()  
    df_pesanan = get_pesanan_dataframe()  
    df_hasil_rekomendasi = content_based_filtering(id_konsumen_tertentu, df_pesanan, df_menu)
    top_10_menu = df_hasil_rekomendasi[:10]
    recommended_menu_ids = [menu['id_menu'] for menu in top_10_menu]
    return jsonify({'recommended_menu_ids': recommended_menu_ids})

if __name__ == '__main__':
    app.run(debug=True)