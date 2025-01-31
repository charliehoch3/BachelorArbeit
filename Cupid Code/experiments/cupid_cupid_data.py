import itertools
import os
import pandas as pd
import numpy as np

from algorithms.cupid.cupid_model import Cupid
from experiments.cupid_experiments import run_experiments, compute_statistics

CURRENT_DIR = os.path.dirname(__file__)
# Die Normalisierung der Eingaben findet in cupid_model -> create_cupid_element statt.
#  Die Sprache dafür wird einfach als default "englisch" gelassen.
#  Deswegen wäre es gut, wenn die beiden Eingabedateien auch englisch sind.
DSE_SCHEMA = CURRENT_DIR + '/../data/cupid/paper/datenschutzerkaerung.csv'
DA_SCHEMA = CURRENT_DIR + '/../data/cupid/paper/datenauskunft.csv'


def make_model(file_path1, file_path2):
    cupid_model = Cupid()

    df1 = pd.read_csv(file_path1)
    cupid_model.add_original(df1)
    table_list1 = df1['table'].unique()

    for table in table_list1:
        column_data1 = zip(df1[df1['table'] == table]['column'], itertools.repeat('string'))
        # schema_name, table_name, pairs of (column_name, data_type)
        cupid_model.add_data(file_path1, table, column_data1)

    df2 = pd.read_csv(file_path2)
    cupid_model.add_original(df2)

    table_list2 = df2['table'].unique()

    for table in table_list2:
        column_data2 = zip(df2[df2['table'] == table]['column'], itertools.repeat('string'))
        cupid_model.add_data(file_path2, table, column_data2)

    return cupid_model


if __name__ == "__main__":
    cupid_model = make_model(DSE_SCHEMA, DA_SCHEMA)
    source_tree = cupid_model.get_schema_by_index(0)
    target_tree = cupid_model.get_schema_by_index(1)

    out_dir = CURRENT_DIR + '/cupid-output/'
    gold_standard_file = CURRENT_DIR + '/../data/cupid/paper/cupid-paper-gold.txt'
    leaf_range = np.arange(0.1, 1, 0.1)
    th_accept_range = np.arange(0.05, 0.5, 0.02)

    print(cupid_model.print_data())

    run_experiments(source_tree, target_tree, cupid_model, out_dir, leaf_range, th_accept_range)
    compute_statistics(gold_standard_file, out_dir, leaf_range, th_accept_range)

