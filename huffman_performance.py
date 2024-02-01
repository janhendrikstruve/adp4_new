import pandas as pd
import matplotlib.pyplot as plt

# Daten als Dictionary
df = pd.read_csv("huffman_performance.csv")

# Gesamtdiagramm erstellen und speichern
fig, ax = plt.subplots(2, 1, figsize=(10, 8))

# Kodierungs- und Dekodierungszeit
ax[0].bar(df['Dateiname'], df['Kodierungszeit (ms)'], color='blue', label='Kodierungszeit')
ax[0].bar(df['Dateiname'], df['Dekodierungszeit (ms)'], bottom=df['Kodierungszeit (ms)'], color='green', label='Dekodierungszeit')
ax[0].set_ylabel('Zeit (ms)')
ax[0].set_title('Kodierungs- und Dekodierungszeit')
ax[0].tick_params(axis='x', rotation=45)
ax[0].legend()

# Dateigrößenänderung
ax[1].bar(df['Dateiname'], df['Originalgröße (Bytes)'], color='red', label='Originalgröße')
ax[1].bar(df['Dateiname'], df['Kodierte Größe (Bytes)'], color='orange', label='Kodierte Größe')
ax[1].set_ylabel('Größe (Bytes)')
ax[1].set_title('Dateigröße vor und nach der Kodierung')
ax[1].tick_params(axis='x', rotation=45)
ax[1].legend()

plt.tight_layout()

# Speichern des Gesamtdiagramms
plt.savefig('huffman_performance.png')

# Erstellen und Speichern eines Diagramms nur für List.use und shakespeare.txt
df_filtered = df[df['Dateiname'].isin(['List.use', 'shakespeare.txt'])]

fig, ax = plt.subplots(2, 1, figsize=(10, 6))

# Kodierungs- und Dekodierungszeit für gefilterte Daten
ax[0].bar(df_filtered['Dateiname'], df_filtered['Kodierungszeit (ms)'], color='blue', label='Kodierungszeit')
ax[0].bar(df_filtered['Dateiname'], df_filtered['Dekodierungszeit (ms)'], bottom=df_filtered['Kodierungszeit (ms)'], color='green', label='Dekodierungszeit')
ax[0].set_ylabel('Zeit (ms)')
ax[0].set_title('Kodierungs- und Dekodierungszeit (gefiltert)')
ax[0].legend()

# Dateigrößenänderung für gefilterte Daten
ax[1].bar(df_filtered['Dateiname'], df_filtered['Originalgröße (Bytes)'], color='red', label='Originalgröße')
ax[1].bar(df_filtered['Dateiname'], df_filtered['Kodierte Größe (Bytes)'], color='orange', label='Kodierte Größe')
ax[1].set_ylabel('Größe (Bytes)')
ax[1].set_title('Dateigröße vor und nach der Kodierung (gefiltert)')
ax[1].legend()

plt.tight_layout()

# Speichern des gefilterten Diagramms
plt.savefig('huffman_performance_gefiltert.png')