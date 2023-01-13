import {ElNotification} from 'element-plus'

export function jsonPretty(str: string, tab?: number | undefined): string {
  const obj = JSON.parse(str)
  return tab == undefined ?
      JSON.stringify(obj, null, 2) :
      JSON.stringify(obj, null, tab);
}

export function vAlert(title: string,
                       message: string,
                       type?: string | 'success',
                       position?: string | 'bottom-right'): void {
  ElNotification({
    title: '操作',
    message: '操作成功',
    type: 'success',
    position: 'bottom-right'
  })
}

export function objectEmpty(obj: any): boolean {
  return obj && Object.keys(obj).length == 0
}

export class Page {
  pageNum: number;
  pageSize: number;
  total: number;

  constructor(pageNum: number, pageSize: number, total: number) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.total = total;
  }

}
