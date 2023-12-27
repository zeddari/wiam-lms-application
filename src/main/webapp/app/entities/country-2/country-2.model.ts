export interface ICountry2 {
  id: number;
  countryName?: string | null;
}

export type NewCountry2 = Omit<ICountry2, 'id'> & { id: null };
