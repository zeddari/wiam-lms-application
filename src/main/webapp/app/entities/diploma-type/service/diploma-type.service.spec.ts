import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDiplomaType } from '../diploma-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../diploma-type.test-samples';

import { DiplomaTypeService } from './diploma-type.service';

const requireRestSample: IDiplomaType = {
  ...sampleWithRequiredData,
};

describe('DiplomaType Service', () => {
  let service: DiplomaTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IDiplomaType | IDiplomaType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DiplomaTypeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DiplomaType', () => {
      const diplomaType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(diplomaType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DiplomaType', () => {
      const diplomaType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(diplomaType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DiplomaType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DiplomaType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DiplomaType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a DiplomaType', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addDiplomaTypeToCollectionIfMissing', () => {
      it('should add a DiplomaType to an empty array', () => {
        const diplomaType: IDiplomaType = sampleWithRequiredData;
        expectedResult = service.addDiplomaTypeToCollectionIfMissing([], diplomaType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diplomaType);
      });

      it('should not add a DiplomaType to an array that contains it', () => {
        const diplomaType: IDiplomaType = sampleWithRequiredData;
        const diplomaTypeCollection: IDiplomaType[] = [
          {
            ...diplomaType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDiplomaTypeToCollectionIfMissing(diplomaTypeCollection, diplomaType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DiplomaType to an array that doesn't contain it", () => {
        const diplomaType: IDiplomaType = sampleWithRequiredData;
        const diplomaTypeCollection: IDiplomaType[] = [sampleWithPartialData];
        expectedResult = service.addDiplomaTypeToCollectionIfMissing(diplomaTypeCollection, diplomaType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diplomaType);
      });

      it('should add only unique DiplomaType to an array', () => {
        const diplomaTypeArray: IDiplomaType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const diplomaTypeCollection: IDiplomaType[] = [sampleWithRequiredData];
        expectedResult = service.addDiplomaTypeToCollectionIfMissing(diplomaTypeCollection, ...diplomaTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const diplomaType: IDiplomaType = sampleWithRequiredData;
        const diplomaType2: IDiplomaType = sampleWithPartialData;
        expectedResult = service.addDiplomaTypeToCollectionIfMissing([], diplomaType, diplomaType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diplomaType);
        expect(expectedResult).toContain(diplomaType2);
      });

      it('should accept null and undefined values', () => {
        const diplomaType: IDiplomaType = sampleWithRequiredData;
        expectedResult = service.addDiplomaTypeToCollectionIfMissing([], null, diplomaType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diplomaType);
      });

      it('should return initial array if no DiplomaType is added', () => {
        const diplomaTypeCollection: IDiplomaType[] = [sampleWithRequiredData];
        expectedResult = service.addDiplomaTypeToCollectionIfMissing(diplomaTypeCollection, undefined, null);
        expect(expectedResult).toEqual(diplomaTypeCollection);
      });
    });

    describe('compareDiplomaType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDiplomaType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDiplomaType(entity1, entity2);
        const compareResult2 = service.compareDiplomaType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDiplomaType(entity1, entity2);
        const compareResult2 = service.compareDiplomaType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDiplomaType(entity1, entity2);
        const compareResult2 = service.compareDiplomaType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
