import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GroupService } from '../service/group.service';
import { IGroup } from '../group.model';
import { GroupFormService } from './group-form.service';

import { GroupUpdateComponent } from './group-update.component';

describe('Group Management Update Component', () => {
  let comp: GroupUpdateComponent;
  let fixture: ComponentFixture<GroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let groupFormService: GroupFormService;
  let groupService: GroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), GroupUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    groupFormService = TestBed.inject(GroupFormService);
    groupService = TestBed.inject(GroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Group query and add missing value', () => {
      const group: IGroup = { id: 456 };
      const group1: IGroup = { id: 14291 };
      group.group1 = group1;

      const groupCollection: IGroup[] = [{ id: 26313 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const additionalGroups = [group1];
      const expectedCollection: IGroup[] = [...additionalGroups, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(
        groupCollection,
        ...additionalGroups.map(expect.objectContaining),
      );
      expect(comp.groupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const group: IGroup = { id: 456 };
      const group1: IGroup = { id: 14168 };
      group.group1 = group1;

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(comp.groupsSharedCollection).toContain(group1);
      expect(comp.group).toEqual(group);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroup>>();
      const group = { id: 123 };
      jest.spyOn(groupFormService, 'getGroup').mockReturnValue(group);
      jest.spyOn(groupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: group }));
      saveSubject.complete();

      // THEN
      expect(groupFormService.getGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(groupService.update).toHaveBeenCalledWith(expect.objectContaining(group));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroup>>();
      const group = { id: 123 };
      jest.spyOn(groupFormService, 'getGroup').mockReturnValue({ id: null });
      jest.spyOn(groupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: group }));
      saveSubject.complete();

      // THEN
      expect(groupFormService.getGroup).toHaveBeenCalled();
      expect(groupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroup>>();
      const group = { id: 123 };
      jest.spyOn(groupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(groupService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGroup', () => {
      it('Should forward to groupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(groupService, 'compareGroup');
        comp.compareGroup(entity, entity2);
        expect(groupService.compareGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
