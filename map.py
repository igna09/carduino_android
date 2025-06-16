import os
import sys
from urllib import response
import requests
import osmium
import geojson
from datetime import datetime

# === CONFIG ===
PBF_URL = "https://download.geofabrik.de/europe/italy-latest.osm.pbf"
PBF_FILE = "italy-latest.osm.pbf"
OUTPUT_GEOJSON = "italy_speed_limits.geojson"

# === STEP 1: SCARICA IL FILE .PBF ===
def download_pbf(url, filename):
    if os.path.exists(filename):
        print(f"File '{filename}' già presente, salto download.")
        return
    print(f"Scaricando {url} ...")
    with requests.get(url, stream=True, verify=False) as r:
        r.raise_for_status()
        total_length = r.headers.get('content-length')
        with open(filename, 'wb') as f:
            dl = 0
            total_length = int(total_length)
            print(total_length)
            for chunk in r.iter_content(chunk_size=8192):
                dl += len(chunk)
                f.write(chunk)
                done = int(50 * dl / total_length)
                sys.stdout.write("\r[%s%s]" % ('=' * done, ' ' * (50-done)) )    
                sys.stdout.flush()
    print(f"Download completato: {filename}")

# === STEP 2: PARSA E ESTRAI MAXSPEED ===
class SpeedLimitHandler(osmium.SimpleHandler):
    def __init__(self):
        super().__init__()
        self.features = []

    def way(self, w):
        if 'highway' in w.tags and 'maxspeed' in w.tags and len(w.nodes) > 1:
            coordinates = [(n.lon, n.lat) for n in w.nodes]
            props = {
                "highway": w.tags.get("highway"),
                "maxspeed": w.tags.get("maxspeed")
            }
            line = geojson.Feature(geometry=geojson.LineString(coordinates), properties=props)
            self.features.append(line)

# === STEP 3: SALVA IN GEOJSON ===
def extract_and_save_geojson(pbf_file, output_file):
    print(f"Estraendo i limiti di velocità da {pbf_file}...")
    handler = SpeedLimitHandler()
    handler.apply_file(pbf_file)
    fc = geojson.FeatureCollection(handler.features)

    with open(output_file, 'w', encoding='utf-8') as f:
        geojson.dump(fc, f, indent=2)

    print(f"File GeoJSON salvato in: {output_file}")
    print(f"Totale segmenti trovati: {len(handler.features)}")

# === MAIN ===
if __name__ == "__main__":
    start = datetime.now()
    download_pbf(PBF_URL, PBF_FILE)
    extract_and_save_geojson(PBF_FILE, OUTPUT_GEOJSON)
    print("Tempo totale:", datetime.now() - start)